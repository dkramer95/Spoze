// constant representing combined model/view/projection matrix
uniform mat4 u_MVPMatrix;

// per-vertex position we will pass in
attribute vec4 a_Position;

// per-vertex color we will pass in
attribute vec4 a_Color;

// This will be passed into the fragment shader
varying vec4 v_Color;

attribute float a_PointSize;

void main() {
    // pass color through to fragment shader
    // it will be interpolated
    v_Color = a_Color;

    // Multiply vertex by matrix to get final position in normalized screen coordinates
    gl_Position = u_MVPMatrix * a_Position;

    gl_PointSize = a_PointSize;
}

