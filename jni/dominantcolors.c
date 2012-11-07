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

#define  LOG_TAG    "dominantcolors"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct {
	double red;
	double green;
	double blue;
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

static int red(uint32_t color) {
  return (int) ((color & 0x00FF0000) >> 16);
}

static int green(uint32_t color) {
  return (int) ((color & 0x0000FF00) >> 8);
}

static int blue(uint32_t color) {
  return (int) (color & 0x000000FF);
}

static uint32_t color(int r, int g, int b) {
  return (0xFF000000 | (r << 16) & 0x00FF0000) |
          ((g << 8) & 0x0000FF00) |
          (b & 0x000000FF);
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
	  LOGI("%X\n", new_color);
	  centroids[filled++] = new_color;
    }
  }

  double max_error;
  int index = 0;

  do {
  	// reset vars
    max_error = 0;
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
		sums[best_centroid].red += red(line[xx]);
		sums[best_centroid].green += green(line[xx]);
		sums[best_centroid].blue += blue(line[xx]);
		members[best_centroid]++;
	  }
	  line = (uint32_t*) ((char*)line + info->stride);
	}
	for (c = 0; c < numColors; c++) {
	  uint32_t new_centroid;
	  if (members[c] == 0) {
		new_centroid = 0xFFFFFFFF;
	  } else {
		new_centroid = color(sums[c].red/members[c],
		  sums[c].green/members[c],
		  sums[c].blue/members[c]);
	  }
	  double dist = distance(new_centroid, centroids[c]);
	  if (dist > max_error) {
	    max_error = dist;
	  }
	  LOGI("\t%d new centroid: %X\n", index, new_centroid);
	  centroids[c] = new_centroid;
    }
    LOGI("\tmax error:%f\n", max_error);
  } while (index++ < 100 && max_error > 1);
  LOGI("iterations: %d\n", index);
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
  
  (*env)->SetIntArrayRegion(env, result, 0, numColors, fill);
	
  AndroidBitmap_unlockPixels(env, bitmap);
  return result;
}
