package org.matsim.analysis;

import org.locationtech.jts.geom.Geometry;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;

import java.util.stream.Collectors;

public class CountActivitiesInMitte {

    public static void main(String[] args) {

        var shapeFileName = "E:\\currentWORK\\dataMATSim\\Berlin_Bezirksgrenzen_-2292027283764261881\\Berlin_Bezirke.shp";
        var plansFileName = "E:\\currentWORK\\dataMATSim\\berlin-v5.5.3-1pct.output_plans.xml.gz";
        var transformation = TransformationFactory.getCoordinateTransformation("EPSG:31468", "EPSG:3857");

        var features = ShapeFileReader.getAllFeatures(shapeFileName);

        var geometries = features.stream()
                .filter(simpleFeature -> simpleFeature.getAttribute("Gemeinde_s").equals("001"))
                .map(simpleFeature -> (Geometry) simpleFeature.getDefaultGeometry())
                .collect(Collectors.toList());

        var mitteGeometry = geometries.get(0);

        var population = PopulationUtils.readPopulation(plansFileName);

        var counter = 0;

        for (Person person : population.getPersons().values()){

            var plan = person.getSelectedPlan();
            var activities = TripStructureUtils.getActivities(plan, TripStructureUtils.StageActivityHandling.ExcludeStageActivities);

            for (Activity activity : activities){

                var coord = activity.getCoord();
                var transformedCoor = transformation.transform(coord);
                var geotoolsPoints = MGC.coord2Point(transformedCoor);

                if (mitteGeometry.contains(geotoolsPoints)){
                    counter++;
                }
            }
        }

        System.out.println(counter + " activities in Mitte.");

    }
}
