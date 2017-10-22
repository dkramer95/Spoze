precision mediump float;

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;

void main() {
    // u_TextureUnit contains actual texture data
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}
