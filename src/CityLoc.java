
public class CityLoc {
	public double CenterLat;
	public double CenterLong;
	public double SouthwestLat;
	public double SouthwestLong;
	public double NortheastLat;
	public double NortheastLong;
	
	public CityLoc(double _centerLat, double _centerLong,
			double _southwestLat, double _southwestLong, 
			double _northeastLat, double _northeastLong) {
		CenterLat = _centerLat;
		CenterLong = _centerLong;
		SouthwestLat = _southwestLat;
		SouthwestLong = _southwestLong;
		NortheastLat = _northeastLat;
		NortheastLong = _northeastLong;
	}

}

