precision mediump float;

uniform sampler2D u_Texture;
varying vec2 v_TextureCoordinates;

void main() {
    // u_TextureUnit contains actual texture data
    gl_FragColor = texture2D(u_Texture, v_TextureCoordinates);
}
