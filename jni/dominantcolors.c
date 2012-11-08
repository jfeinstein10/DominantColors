#include <jni.h>
#include <time.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

typedef int bool;
#define true 1
#define false 0
#define alpha_mask 0xFF000000
#define blue_mask 0x00FF0000
#define blue_shift 16
#define green_mask 0x0000FF00
#define green_shift 8
#define red_mask 0x000000FF

#define  LOG_TAG    "dominantcolors"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct {
	double r;
	double g;
	double b;
} color_sum;

static int rgb_clamp(int value) {
  if(value > 255) {
    return 255;
  }
  if(value < 0) {
    return 0;
  }
  return value;
}

static int blue(uint32_t color) {
  return (int) ((color & blue_mask) >> blue_shift);
}

static int green(uint32_t color) {
  return (int) ((color & green_mask) >> green_shift);
}

static int red(uint32_t color) {
  return (int) (color & red_mask);
}

static uint32_t color(int r, int g, int b) {
  return (alpha_mask | ((b << blue_shift) & blue_mask) |
          ((g << green_shift) & green_mask) |
          (r & red_mask));
}

static uint32_t rev_color(uint32_t c) {
	return color(blue(c), green(c), red(c));
}

static double distance(uint32_t c1, uint32_t c2) {
  return sqrt(pow(red(c1)-red(c2),2) + 
    pow(green(c1)-green(c2),2) + 
    pow(blue(c1)-blue(c2),2));
}

static void kmeans(AndroidBitmapInfo* info, void* pixels, int numColors, jint* centroids){
  int xx, yy, c;
  uint32_t* start = (uint32_t*)pixels;
  uint32_t* line;
  
  color_sum sums[numColors];
  double members[numColors];
  int filled = 0;
  uint32_t new_color;
  while (filled < numColors) {
    xx = rand() % info->width;
	yy = rand() % info->height;
	new_color = ((uint32_t*) ((char *)pixels + yy*info->stride))[xx];
	bool contained = false;
	int i;
	for (i = 0; i < filled; contained |= (centroids[i++] == new_color));
	if (!contained) {
	  centroids[filled++] = new_color;
    }
  }

  double max_error;
  int index = 0;

  do {
  	// reset vars
	for (c = 0; c < numColors; c++) {
	  sums[c] = (color_sum) { 0, 0, 0 };
	  members[c] = 0;
	}
	// start from the beginning of the image
	line = start;
	for (yy = 0; yy < info->height; yy++){
	  for (xx = 0; xx < info->width; xx++){
		double min_dist = DBL_MAX;
		int best_centroid = 0;
		for (c = 0; c < numColors; c++) {
		  double dist = distance(line[xx], centroids[c]);
		  if (dist < min_dist) {
			min_dist = dist;
			best_centroid = c;
		  }
		}
		sums[best_centroid].r += red(line[xx]);
		sums[best_centroid].g += green(line[xx]);
		sums[best_centroid].b += blue(line[xx]);
		members[best_centroid]++;
	  }
	  line = (uint32_t*) ((char*)line + info->stride);
	}
    max_error = 0;
	for (c = 0; c < numColors; c++) {
	  uint32_t new_centroid;
	  if (members[c] == 0) {
		new_centroid = 0xFFFFFFFF;
	  } else {
		new_centroid = color(sums[c].r/members[c],
		  sums[c].g/members[c],
		  sums[c].b/members[c]);
	  }
	  double dist = distance(new_centroid, centroids[c]);
	  if (dist > max_error) {
	    max_error = dist;
	  }
	  centroids[c] = new_centroid;
    }
  } while (index++ < 100 && max_error > 1);
}


JNIEXPORT jintArray JNICALL Java_com_dominantcolors_DominantColors_kmeans(JNIEnv * env, jobject  obj, jobject bitmap, jint numColors)
{
  AndroidBitmapInfo info;
  int ret;
  void* pixels;
	
  if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
    LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
    return NULL;
  }
    
  if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
    LOGE("Bitmap format is not RGBA_8888!");
    return NULL;
  }

  if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
    LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
  }    
    
  jintArray result;
  result = (*env)->NewIntArray(env, numColors);
  if (result == NULL)
  	return NULL;
  jint fill[numColors];
  kmeans(&info, pixels, numColors, fill);  
  int i; 
  for (i = 0; i < numColors; i++) {
  	fill[i] = rev_color(fill[i]);
  }
  
  (*env)->SetIntArrayRegion(env, result, 0, numColors, fill);
	
  AndroidBitmap_unlockPixels(env, bitmap);
  return result;
}
