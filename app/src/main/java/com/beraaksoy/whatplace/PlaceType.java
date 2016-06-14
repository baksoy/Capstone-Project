package com.beraaksoy.whatplace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beraaksoy on 5/13/16.
 */
public class PlaceType {

    private static final int TYPE_FLORIST = 37;
    private static final int TYPE_BAKERY = 7;
    private static final int TYPE_GROCERY_OR_SUPERMARKET = 43;
    private static final int TYPE_HEALTH = 47;
    private static final int TYPE_STORE = 88;
    private static final int TYPE_FOOD = 38;
    private static final int TYPE_POINT_OF_INTEREST = 1013;
    private static final int TYPE_ESTABLISHMENT = 34;
    private static final int TYPE_CAFE = 15;

    public static List<String> getPlaceTypesString(List<Integer> placeTypes) {

        List<String> placeTypesString = new ArrayList<>();

        for (Integer integer : placeTypes) {

            switch (integer) {

                case PlaceType.TYPE_FLORIST:
                    placeTypesString.add("FLORIST");
                    break;

                case PlaceType.TYPE_BAKERY:
                    placeTypesString.add("BAKERY");
                    break;

                case PlaceType.TYPE_ESTABLISHMENT:
                    placeTypesString.add("ESTABLISHMENT");
                    break;

                case PlaceType.TYPE_GROCERY_OR_SUPERMARKET:
                    placeTypesString.add("GROCERY");
                    break;

                case PlaceType.TYPE_HEALTH:
                    placeTypesString.add("HEALTH");
                    break;

                case PlaceType.TYPE_STORE:
                    placeTypesString.add("STORE");
                    break;

                case PlaceType.TYPE_FOOD:
                    placeTypesString.add("FOOD");
                    break;

                case PlaceType.TYPE_CAFE:
                    placeTypesString.add("CAFE");
                    break;

                case PlaceType.TYPE_POINT_OF_INTEREST:
                    placeTypesString.add("POINT OF INTEREST");
                    break;
                    
                default:
                    placeTypesString.add("UNKNOWN");
                    break;
            }
        }
        return placeTypesString;
    }
}
