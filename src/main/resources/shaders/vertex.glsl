#version 330 core
layout (location=0) in vec3 position;
layout (location=1) in vec4 color;

uniform mat4 projection;
uniform mat4 view;

out vec4 fColor;

void main()
{
    fColor = color;
    gl_Position = projection * view * vec4(position, 1.0);
}