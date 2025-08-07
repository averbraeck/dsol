Map obtained from netherlands-latest.osm.pbf from Geofabrik at https://download.geofabrik.de/
Using osmium-tool v1.14.0

Extraction: 
  osmium extract -b 4.355,51.995,4.386,52.005 -s smart -S complete-partial-relations=1 -o tudelft.osm.pbf netherlands-latest.osm.pbf 

Conversion:
  osmium cat tudelft.osm.pbf -o tudelft.osm.gz
