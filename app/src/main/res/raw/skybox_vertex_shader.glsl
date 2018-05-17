uniform mat4 u_Matrix;
attribute vec3 a_Position;
varying vec3 v_Position;
varying vec4 ambient;
varying vec4 specular;
uniform mat4 u_MVMatrix;
uniform mat4 u_IT_MVMatrix;
uniform mat4 u_MVPMatrix;
attribute vec3 a_Normal;
uniform vec4 u_PointLightPositions;    // In eye space
uniform vec3 u_PointLightColors;
vec4 eyeSpacePosition;
vec3 eyeSpaceNormal;
vec4 getPointLighting();
void main()                    
{
     ambient = vec4(0.2,0.2,0.2,1.0);
     eyeSpacePosition = u_MVMatrix * vec4(a_Position,1);
     eyeSpaceNormal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));
     specular = getPointLighting();

    v_Position = a_Position;
    v_Position.z = -v_Position.z;
	           
    gl_Position = u_Matrix * vec4(a_Position, 1.0);
}    
vec4 getPointLighting()
{
    vec3 lightingSum = vec3(0.0);

    vec3 toPointLight = vec3(u_PointLightPositions) - vec3(eyeSpacePosition);
    float distance = length(toPointLight);
    toPointLight = normalize(toPointLight);
    float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);
    lightingSum += (u_PointLightColors * 5.0 * cosine)/distance;
    return vec4(lightingSum,1.0);
}