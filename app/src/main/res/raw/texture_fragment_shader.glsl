precision mediump float;

uniform sampler2D u_Texture;
varying vec2 v_TextureCoordinates;

void main() {
    // u_TextureUnit contains actual texture data
    vec4 val = texture2D(u_Texture, v_TextureCoordinates);
    if (val.a > 0.5) {
        gl_FragColor = val;
    } else {
        discard;
    }
//    gl_FragColor = texture2D(u_Texture, v_TextureCoordinates);
}
