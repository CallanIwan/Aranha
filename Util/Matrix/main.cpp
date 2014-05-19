//
//  main.cpp
//  vector
//
//  Created by Callan Kandasamy on 16-05-14.
//  Copyright (c) 2014 aranha. All rights reserved.
//

#include <iostream>
#include "vector3.h"
#include "Matrix.h"

int main(int argc, const char * argv[])
{

    // insert code here...
    vector3 vec1 = vector3(1,0,1);
    vector3 vec2 = vector3(2,1,2);
    vector3 vec3;
    vec3 = vec1 - vec2;
    std::cout << vec3.x << vec3.y << vec3.z << std::endl;
    
    Matrix matrix1 = Matrix(2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2);
    Matrix matrix2 = Matrix(2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2);
    
    matrix1.print();
    matrix2.print();
    
    Matrix matrix3 = matrix1 * matrix2;
    matrix3 = matrix3 * 3;
    matrix3.print();
    
    vector3 v3 = vector3();
    v3 = v3.Transform(vec2, matrix2);
    std::cout << v3.x << v3.y << v3.z << std::endl;
    return 0;
}

