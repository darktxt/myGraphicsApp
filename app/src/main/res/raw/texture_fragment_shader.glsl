precision mediump float;

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;
vec4 baseColor;
varying vec4 ambient;
varying vec4 specular;
varying vec4 diffuse;
void main()
{
    baseColor = texture2D(u_TextureUnit,v_TextureCoordinates);
    gl_FragColor = baseColor*ambient+baseColor*specular+baseColor*diffuse;
}