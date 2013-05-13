// SingleTriangleDemo Vertex Shader

#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec4 in_Position;
in vec4 in_Color;

out vec4 pass_Color;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * in_Position;
	
	pass_Color = in_Color;
}