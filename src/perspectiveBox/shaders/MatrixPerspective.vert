#version 400

uniform mat4 projectionMatrix;
uniform vec3 offset;

in vec4 in_Position;
in vec4 in_Color;


out vec4 pass_Color;

void main() {
	vec4 cameraPos = in_Position + vec4(offset.x, offset.y, offset.z, 0.0);
	
	gl_Position = projectionMatrix * cameraPos;
	
	pass_Color = in_Color;
}