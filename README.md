# road-dist-optimiser
Solving a VRP using Road Distances

## Objective

To make JSPRIT work with OSRM.

End result can be a backend tool as a POC.

Objective is to time the additional delay, especially at scale of points (200 points?)


## Background

JSPRIT uses straight line distances for optimisation.

This often produces sub-optimal decisions, especially when road constraints come into play, e.g. single direction traffic.

As a trivial example, consider this scenario
![Simple Singapore Optimisation](https://raw.githubusercontent.com/ariua91/road-dist-optimiser/dev/readme_imgs/straight_line.PNG)

This route has a striaght-line distance of 3,991.16 M and a road distance of 13,032 M.

Some flaws are apparent:
 1. To get to stop 5 from stop 4 will require travelling past stop 6
 2. Start point is on a side of the road that does not allow quick access to stop 1

Reversing flaw 1 gives a route with SL distance 4,403.19 M (longer) but a road distance of 12,210 M.
![Simple Change to reverse flaw 1](https://raw.githubusercontent.com/ariua91/road-dist-optimiser/dev/readme_imgs/swap%205%206.png)

Reversing flaw 2 gives a route with a still longer SL distance of 4,402.95 M but a significantly shorter road distance of 7,004 M.
![Large Change to reverse flaw 2](https://raw.githubusercontent.com/ariua91/road-dist-optimiser/dev/readme_imgs/revamp.png)

## Guide

### Set up OSRM

This build catered for Windows - should be easier on linux.
Also possible to [run as C++ API](http://project-osrm.org/docs/v5.5.1/api/#introduction) for speed - I leave that for a better coder. 

 1. Get OSRM build for windows http://build.project-osrm.org/
 2. Get SG OSRM map http://download.geofabrik.de/asia.html
 3. Run OSRM https://github.com/Project-OSRM/osrm-backend/wiki/Running-OSRM
    ```
    osrm-extract.exe malaysia-singapore-brunei-latest.osm.pbf -p car.lua
    osrm-partition malaysia-singapore-brunei-latest.osrm
    osrm-customize malaysia-singapore-brunei-latest.osrm

    osrm-routed --algorithm=MLD malaysia-singapore-brunei-latest.osrm
    ```
    API docs: http://project-osrm.org/docs/v5.5.1/api/#general-options
    Will be avail on http://localhost:5000

### Set up JSPRIT

 1. Make sure you have JDK
 2. Get maven (optional, you can get jsprit from git)
 3. 
