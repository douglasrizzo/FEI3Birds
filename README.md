# FEI3Agent
Angry Birds AI Agent

FEI3Agent is an intelligent agent created by graduate students of Centro Universitário FEI to participate in the 2013 [Angry Birds AI Competition](https://aibirds.org/), hosted during the International Joint Conferences in Artificial Intelligence.

It is an agent that collects a low and a high trajectory from the bird to every target pig and chooses the one with least resistance, given the bird type. In this way, yellow birds give preference to trajectories with wooden blocks; blue birds prefer trajectories with ice blocks; and all birds prefer trajectories with no walls or obstacles.

## Theory

Given a bird _b_ and a set of objects _O<sub>t</sub>_ that cross trajectory _t_, the total resistance _R_ of _t_ is given by:

![Resistance function](http://www.sciweavers.org/tex2img.php?eq=R_t%20%3D%20%5Csum_%20%7Bo%20%5Cin%20O_t%7D%20A%5Bb%2C%20o%5D&bc=White&fc=Black&im=jpg&fs=12&ff=arev&edit=0)

where _A_ is a matrix containing the weights associated between bird types and obstacle types:

![Weight matrix](http://www.sciweavers.org/tex2img.php?eq=A%3D%0A%5Cbegin%7Bbmatrix%7D%0A1%20%26%206%20%26%202%20%26%20%5Cinfty%20%5C%5C%0A3%20%26%201%20%26%206%20%26%20%5Cinfty%20%5C%5C%0A1%20%26%206%20%26%203%20%26%20%5Cinfty%20%5C%5C%0A%5Cend%7Bbmatrix%7D&bc=White&fc=Black&im=jpg&fs=12&ff=arev&edit=0)

In _A_, rows represent red, blue and yellow birds and columns represent wood, ice, stone and unbreakable obstacles, respectively.

The optimal trajectory _t*_ is chosen from the set _T_ of all possible trajectories between the current bird and all pigs as the one that minimizes the resistance function:

![Preference function](http://www.sciweavers.org/tex2img.php?eq=t%2A%20%3D%20argmin_%7Bt%20%5Cin%20T%7D%20%20R_t&bc=White&fc=Black&im=jpg&fs=12&ff=arev&edit=0)

## Architecture

The agent is written in Java. It was built on top of the an example provided in the competition website. We used version 1.32 of the competition client.

For more information, please visit the [official tutorial](https://aibirds.org/basic-game-playing-software/getting-started.html).
