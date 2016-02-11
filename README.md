# FEI3Agent
Angry Birds AI Agent

FEI3Agent is an intelligent agent created by graduate students of Centro Universitário FEI to participate in the 2013 [Angry Birds AI Competition](https://aibirds.org/), hosted during the International Joint Conferences in Artificial Intelligence.

It is an agent that collects a low and a high trajectory from the bird to every target pig and chooses the one with least resistance, given the bird type. In this way, yellow birds give preference to trajectories with wooden blocks; blue birds prefer trajectories with ice blocks; and all birds prefer trajectories with no walls or obstacles.

## Theory

Given a bird _b_ and a set of objects _O<sub>t</sub>_ that cross trajectory _t_, the total resistance _R_ of _t_ is given by:

![R_t = \sum_ {o \in O_t} A[b, o]](http://latex.numberempire.com/render?R_t%20%3D%20%5Csum_%20%7Bo%20%5Cin%20O_t%7D%20A%5Bb%2C%20o%5D&sig=6b76d502b2484cd02860dc5bb17f8bdf)

where _A_ is a matrix containing the weights associated between bird types and obstacle types:

![\begin{bmatrix}1 & 6 & 2 & \infty \\3 & 1 & 6 & \infty \\1 & 6 & 3 & \infty \\\end{bmatrix}](http://latex.numberempire.com/render?A%20%3D%20%5Cbegin%7Bbmatrix%7D1%20%26%206%20%26%202%20%26%20%5Cinfty%20%5C%5C3%20%26%201%20%26%206%20%26%20%5Cinfty%20%5C%5C1%20%26%206%20%26%203%20%26%20%5Cinfty%20%5C%5C%5Cend%7Bbmatrix%7D&sig=a85a786ead8b04d620fccb131220d8ef)

In _A_, rows represent red, blue and yellow birds and columns represent wood, ice, stone and unbreakable obstacles, respectively.

The optimal trajectory _t*_ is chosen from the set _T_ of all possible trajectories between the current bird and all pigs as the one that minimizes the resistance function:

![t* = argmin_{t \in T} \quad R_t](http://latex.numberempire.com/render?t*%20%3D%20argmin_{t%20\in%20T}%20\quad%20R_t&sig=086f5680a4b9c10d7e8eb2da01e1878b)

## Architecture

The agent is written in Java. It was built on top of the an example provided in the competition website. We used version 1.32 of the competition client.

For more information, please visit the [official tutorial](https://aibirds.org/basic-game-playing-software/getting-started.html).
