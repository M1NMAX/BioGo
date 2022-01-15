package dev.biogo.Helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;

public class LocationHelper {

    public static String getAddressFromLatLng(Context context, double lat, double lng){
        String result = null;
        Geocoder geocoder = new Geocoder(context);
        try {
            Address address = geocoder
                    .getFromLocation(lat, lng, 1)
                    .get(0);
            result = address.getThoroughfare() + ", " + address.getLocality() + ", " + address.getCountryName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }


}
