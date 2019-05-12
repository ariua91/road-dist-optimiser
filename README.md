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
    ```
    mvn compile assembly:single
    java -cp target/vrp-jar-with-dependencies.jar com/graphhopper/jsprit/examples/SimpleExample
    java -cp target/vrp-jar-with-dependencies.jar com/graphhopper/jsprit/examples/RoadExample
    ```

## Results

Surprisingly good.

Setting up OSRM is almost trivial.
I expect the C++ implementation to be equally trivial - there should be some wrapper to pass the variables using and get response in JSON.
The real challenge would be to test the speed of this OSRM - input 600, 1,000 points and see what kind of response time we get.

OSRM actually opens a new can of worms - it's too accurate.

Initial routing was pushing a very strange route until investigation showed the pin was on an underpass, so routing was sending the vehicle on a huge roundabout to enter the underpass.

This may work better on home lat lngs as compared to road side points


## Immediate improvements

 1. Need to configure jsprit to accept xml / text file for veh & points
    Can't keep recompiling java to change points.
 2. Would be nice to have a visualiser of the OSRM route
    Currently using https://map.project-osrm.org/?z=15&center=1.268780%2C103.823703&loc=1.269991%2C103.814199&loc=1.268018%2C103.825527&loc=1.272440%2C103.823819&loc=1.274705%2C103.821135&loc=1.274123%2C103.825550&loc=1.275674%2C103.819127&loc=1.280351%2C103.823897&loc=1.275823%2C103.830146&hl=en&alt=0
 3. Test this on some home addresses, not these road side stops
