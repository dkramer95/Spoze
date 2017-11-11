#pragma version(1)
#pragma rs java_package_name(edu.neumont.dkramer.spoze3);
#pragma rs_fp_relaxed

rs_allocation yuv_in;
rs_allocation extra_alloc;

float threshold;

static float distSq(uchar r1, uchar g1, uchar b1, uchar r2, uchar g2, uchar b2);

uchar4 __attribute__((kernel)) convert(uint32_t x, uint32_t y) {
    uchar Y1 = rsGetElementAtYuv_uchar_Y(yuv_in, x, y);
    uchar Y2 = rsGetElementAtYuv_uchar_Y(extra_alloc, x, y);

    uchar U1 = rsGetElementAtYuv_uchar_U(yuv_in, x, y);
    uchar U2 = rsGetElementAtYuv_uchar_U(extra_alloc, x, y);

    uchar V1 = rsGetElementAtYuv_uchar_V(yuv_in, x, y);
    uchar V2 = rsGetElementAtYuv_uchar_V(extra_alloc, x, y);

    uchar4 last = rsYuvToRGBA_uchar4(Y1, U1, V1);
    uchar4 current = rsYuvToRGBA_uchar4(Y2, U2, V2);

    float result = distSq(last.r, last.g, last.b, current.r, current.g, current.b);
    uchar4 out;

    //out.rgb = result < (threshold * threshold) ? 0 : 255;
    out.g = result < (threshold * threshold) ? 0 : 255;
    out.b = result < (threshold * threshold) ? 0 : 255;
    out.a = 0;

    return out;
}

static float distSq(uchar r1, uchar g1, uchar b1, uchar r2, uchar g2, uchar b2) {
    float result = (r2 - r1) * (r2 - r1) + (g2 - g1) * (g2 - g1) + (b2 - b1) * (b2 - b1);
    return result;
}
