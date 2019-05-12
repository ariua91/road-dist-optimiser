/*
 * Licensed to GraphHopper GmbH under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * GraphHopper GmbH licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.jsprit.examples;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.box.SchrimpfFactory;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;

import com.graphhopper.jsprit.core.util.RoadTransportCostsMatrix;

import java.io.File;
import java.util.Collection;
import java.net.URL;
import java.io.IOException;

public class RoadExample {


    public static void main(String[] args) throws IOException {
        /*
         * some preparation - create output folder
                 */
        File dir = new File("output");
        // if the directory does not exist, create it
        if (!dir.exists()) {
            System.out.println("creating directory ./output");
            boolean result = dir.mkdir();
            if (result) System.out.println("./output created");
        }

                /*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and one capacity dimension, i.e. weight, and capacity dimension value of 2
                 */
        final int WEIGHT_INDEX = 0;
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType") /*.addCapacityDimension(WEIGHT_INDEX, 2) */;
        VehicleType vehicleType = vehicleTypeBuilder.build();

                /*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
                 */
        Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
        Location startLocation = Location.newInstance(103.8142028, 1.2700152);
        vehicleBuilder.setStartLocation(startLocation);
        vehicleBuilder.setType(vehicleType);
        vehicleBuilder.setReturnToDepot(false);
        VehicleImpl vehicle = vehicleBuilder.build();

                /*
         * build services at the required locations, each with a capacity-demand of 1.
                 */
        // Service service1 = Service.Builder.newInstance("1").addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(103.819179, 1.275799)).build();
        Service service1 = Service.Builder.newInstance("1").setLocation(Location.newInstance(103.825599, 1.267841)).build();
        Service service2 = Service.Builder.newInstance("2").setLocation(Location.newInstance(103.8237914, 1.2724292)).build();
        Service service3 = Service.Builder.newInstance("3").setLocation(Location.newInstance(103.830195, 1.275882)).build();
        Service service4 = Service.Builder.newInstance("4").setLocation(Location.newInstance(103.825566, 1.274187)).build();
        Service service5 = Service.Builder.newInstance("5").setLocation(Location.newInstance(103.821299, 1.274786)).build();
        Service service6 = Service.Builder.newInstance("6").setLocation(Location.newInstance(103.819162, 1.275722)).build();
        Service service7 = Service.Builder.newInstance("7").setLocation(Location.newInstance(103.823744, 1.280356)).build();

        RoadTransportCostsMatrix.Builder builder = RoadTransportCostsMatrix.Builder.newInstance(new URL("http://localhost:5000/table/v1/car/"));
        builder
            .addLocation(startLocation)
            .addLocation(service1.getLocation())
            .addLocation(service2.getLocation())
            .addLocation(service3.getLocation())
            .addLocation(service4.getLocation())
            .addLocation(service5.getLocation())
            .addLocation(service6.getLocation())
            .addLocation(service7.getLocation());
        VehicleRoutingTransportCosts transportCosts = new RoadTransportCostsMatrix(builder);

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);
        vrpBuilder.addJob(service1)
            .addJob(service2)
            .addJob(service3)
            .addJob(service4)
            .addJob(service5)
            .addJob(service6)
            .addJob(service7)
            ;
        vrpBuilder.setRoutingCost(transportCosts);

        VehicleRoutingProblem problem = vrpBuilder.build();

                /*
         * get the algorithm out-of-the-box.
                 */
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

                /*
         * and search a solution
                 */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

                /*
         * get the best
                 */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");

        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

                /*
         * plot
                 */
        new Plotter(problem,bestSolution).plot("output/plot.png","simple example");

        /*
        render problem and solution with GraphStream
         */
        new GraphStreamViewer(problem, bestSolution).labelWith(Label.ID).setRenderDelay(200).display();
    }

}
