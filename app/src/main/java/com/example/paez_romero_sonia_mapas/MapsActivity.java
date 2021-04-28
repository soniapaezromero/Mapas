package com.example.paez_romero_sonia_mapas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author :sonia paez romero  28/04/2021
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Double metrosCuadrados=0.0;
    Double longitud=0.0;
    List coordenadas;
    private Polyline linea;
    private PolygonOptions areaPoligono;
    private Polygon area;

    //Iniciamos el activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    // Creamos el menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }
    // El Agrupamos las distintas opciones
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.metrosCuadrados://LLamamos al metodo que calcula los metros cuadrados
                 obtenerAreaPoligono();
                return true;
            case R.id.perimetro://LLamamos al metodo que  el perimetro
                obtenerPerimetro();
                return true;
            case R.id.borrar://LLamamos borrar poligonos
                 borrarPoligono();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Método que limpia el mapa, y reinicia los parametros  la lista de coordennadas.los metros cuadrados , y la longitud
     */
    private void borrarPoligono() {
        mMap.clear();
        coordenadas.clear();
        metrosCuadrados=0.0;
        longitud=0.0;
        Toast.makeText(MapsActivity.this,"Se ha borrado el poligono",Toast.LENGTH_SHORT).show();

    }

    /**
     * Método que  cierra el poligono y Calcula el área  del poligono
     */
    private void obtenerAreaPoligono() {
        //Cerramos y dibujamos el poligono
        areaPoligono= new PolygonOptions().strokeColor(Color.BLUE).strokeWidth(8);
        for(int i=0;i < getCoordenadas().size();i++){
            areaPoligono.addAll(getCoordenadas());
        }
        area=mMap.addPolygon(areaPoligono);//Le pasamos el objeto poli
        //Calculamos el area con metros cuadrados y mostramos mensaje
        metrosCuadrados= SphericalUtil.computeArea(getCoordenadas());
        String mensaje= "El area tiene "+metrosCuadrados+"m2.";
        Toast.makeText(MapsActivity.this,mensaje,Toast.LENGTH_SHORT).show();


    }

    /**
     * Método que calculamos el perimetro
     */
    private void obtenerPerimetro() {
        //Cerramos el poligono y le pasamos las coordenadas
        areaPoligono= new PolygonOptions().strokeColor(Color.BLUE).strokeWidth(8);
        for(int i=0;i < getCoordenadas().size();i++){
            areaPoligono.addAll(getCoordenadas());
        }
        area=mMap.addPolygon(areaPoligono);
        //Caluclamos el perimetro y le pasamos las coordenadas y mostramos en un toast
        longitud= SphericalUtil.computeLength(getCoordenadas());
        String mensaje= "Tiene un perimetro de:"+longitud+"metros.";
        Toast.makeText(MapsActivity.this,mensaje,Toast.LENGTH_SHORT).show();


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        setCoordenadas(new ArrayList());
        mMap = googleMap;
        // le damos un punto de inicio en malaga
        double latitud = 36.719;
        double longitud = -4.453;
        LatLng malaga = new LatLng(latitud, longitud);
        mMap.addMarker(new MarkerOptions().position(malaga).title("Marker in Málaga (Spain)"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(malaga, 15), 7000, null);
        //Con el click largo,limpiamos el mapa, colocamos el marker en el vertice origen y añadimos siempre que sea el primero a la kista De coordenadas
        mMap.setOnMapLongClickListener((new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point).title("Vértice Origen"));
                //Acercamos la camara al area donde vamos a realizar nuetra area
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15), 7000, null);
                linea=googleMap.addPolyline(new PolylineOptions().clickable(true).color(Color.BLUE).width(8));
                if(getCoordenadas().size()<=0){
                    coordenadas.add(point);
                    String mensaje= " El punto de origen esta en la siguientes coordenadas:"+point;
                    Toast.makeText(MapsActivity.this,mensaje,Toast.LENGTH_SHORT).show();
                }else{
                    mMap.clear();
                }
            }
        }));
        //Con el click corto añadimos el nresto de coordenadas siempre que en la lista hayamos añadido el punto Origen
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
              if(getCoordenadas().size()>0){
                  coordenadas.add(latLng);
                  linea.setPoints(getCoordenadas());
                  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 7000, null);
                  Toast.makeText(MapsActivity.this,"Vértice del añadido:" +latLng,Toast.LENGTH_SHORT).show();
              }else{
                  Toast.makeText(MapsActivity.this,"Tienes que empezar la ruta con un click largo",Toast.LENGTH_SHORT).show();
              }

            }

        });

    }

    /**
     * Getter y Setter Coordenadas
     * @return la Lista de coordenadas necesarias para realizar el poligono
     */
    public List getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(List coordenadas) {
        this.coordenadas = coordenadas;
    }
}