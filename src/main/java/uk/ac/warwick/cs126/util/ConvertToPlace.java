package uk.ac.warwick.cs126.util;

import uk.ac.warwick.cs126.interfaces.IConvertToPlace;
import uk.ac.warwick.cs126.models.Place;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

public class ConvertToPlace implements IConvertToPlace {

    Place[] places;
    public ConvertToPlace() {
        // Initialise things here
        //should i do a sorted one as well so i can binary search or just leave it
        places = getPlacesArray();

    }

    public Place convert(float latitude, float longitude) {
        // TODO
        for(int i =0;i<places.length;i++){
            if(places[i].getLatitude()==latitude&&places[i].getLongitude()==longitude){
                return places[i];
            }
        }
        return new Place("", "", 0.0f, 0.0f);
    }

    public Place[] getPlacesArray() {
        Place[] placeArray = new Place[0];

        try {
            InputStream resource = ConvertToPlace.class.getResourceAsStream("/data/placeData.tsv");
            if (resource == null) {
                String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
                String resourcePath = Paths.get(currentPath, "data", "placeData.tsv").toString();
                File resourceFile = new File(resourcePath);
                resource = new FileInputStream(resourceFile);
            }

            byte[] inputStreamBytes = IOUtils.toByteArray(resource);
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int lineCount = 0;
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (!("".equals(line))) {
                    lineCount++;
                }
            }
            lineReader.close();

            Place[] loadedPlaces = new Place[lineCount - 1];

            BufferedReader tsvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int placeCount = 0;
            String row;

            tsvReader.readLine();
            while ((row = tsvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split("\t");
                    Place place = new Place(
                            data[0],
                            data[1],
                            Float.parseFloat(data[2]),
                            Float.parseFloat(data[3]));
                    loadedPlaces[placeCount++] = place;
                }
            }
            tsvReader.close();

            placeArray = loadedPlaces;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return placeArray;
    }
}

