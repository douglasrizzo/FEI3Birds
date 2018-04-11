# FEI3Agent
Angry Birds AI Agent

FEI3Agent is an intelligent agent created by graduate students of Centro Universitário FEI to participate in the 2013 [Angry Birds AI Competition](https://aibirds.org/), hosted during the International Joint Conferences in Artificial Intelligence.

It is an agent that collects a low and a high trajectory from the bird to every target pig and chooses the one with least resistance, given the bird type. In this way, yellow birds give preference to trajectories with wooden blocks; blue birds prefer trajectories with ice blocks; and all birds prefer trajectories with no walls or obstacles.

## Theory

Given a bird _b_ and a set of objects _O<sub>t</sub>_ that cross trajectory _t_, the total resistance _R_ of _t_ is given by:

![R_t = \sum_ {o \in O_t} A[b, o]](https://latex.codecogs.com/gif.latex?R_t&space;=&space;\sum_&space;{o&space;\in&space;O_t}&space;A[b,&space;o])

where _A_ is a matrix containing the weights associated between bird types and obstacle types:

![\begin{bmatrix}1 & 6 & 2 & \infty \\3 & 1 & 6 & \infty \\1 & 6 & 3 & \infty \\\end{bmatrix}](https://latex.codecogs.com/gif.latex?\begin{bmatrix}1&space;&&space;6&space;&&space;2&space;&&space;\infty&space;\\\\3&space;&&space;1&space;&&space;6&space;&&space;\infty&space;\\\\1&space;&&space;6&space;&&space;3&space;&&space;\infty&space;\end{bmatrix})

In _A_, rows represent red, blue and yellow birds and columns represent wood, ice, stone and unbreakable obstacles, respectively.

The optimal trajectory _t*_ is chosen from the set _T_ of all possible trajectories between the current bird and all pigs as the one that minimizes the resistance function:

![t* = argmin_{t \in T} \quad R_t](https://latex.codecogs.com/gif.latex?t^*&space;=&space;argmin_{t&space;\in&space;T}&space;\quad&space;R_t)

## Architecture

The agent is written in Java. It was built on top of the an example provided in the competition website. We used version 1.32 of the competition client.

For more information, please visit the [official tutorial](https://aibirds.org/basic-game-playing-software/getting-started.html).
