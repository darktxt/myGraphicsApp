precision mediump float;
uniform vec4 u_Color;
varying vec4 ambient;
varying vec4 specular;
varying vec4 diffuse;

void main()
{
    vec4 baseColor = u_Color;
    gl_FragColor = baseColor*ambient+baseColor*specular+baseColor*diffuse;
}