precision mediump float;

//uniform vec4 u_Color;

uniform float u_Color;

void main() {
	gl_FragColor = vec4(u_Color, 0, 0, 1);
}


