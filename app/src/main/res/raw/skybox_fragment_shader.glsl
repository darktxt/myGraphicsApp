precision mediump float; 

uniform samplerCube u_TextureUnit;
varying vec3 v_Position;
varying vec4 ambient;
varying vec4 specular;
void main()                    		
{
	vec4 baseColor = textureCube(u_TextureUnit, v_Position);
    gl_FragColor = baseColor*ambient+baseColor*specular;
}
