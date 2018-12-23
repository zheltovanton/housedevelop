package com.housedevelop.housedevelop;



public enum LengthUnit {
	/**
	 * Miles, using the scale factor 0.6213712 miles per kilometer.
	 */
	MILE(0.6213712),
	/**
	 * Nautical miles, using the scale factor 0.5399568 nautical miles per kilometer.
	 */
	NAUTICAL_MILE(0.5399568),
	/**
	 * Rods, using the scale factor 0.0050292 rods to the kilometer.
	 * Because your car gets forty rods to the hogshead and that's 
	 * they way you likes it.
	 */
	ROD(0.0050292),
	/**
	 * Kilometers, the primary unit.
	 */
	KILOMETER(1.0),
	/**
	 * Meters, for ease of use.
	 */
	METER(1000.0);

	/**
	 * The primary length unit. All scale factors are relative
	 * to this unit. Any conversion not involving the primary
	 * unit will first be converted to this unit, then to 
	 * the desired unit.
	 */
	public static final LengthUnit PRIMARY = KILOMETER;
    public static int[] ZoomInMeters = {51957442,51957442,51957442,27301053,12407563,7107071,3067439,1530533,777642,388357,193999,96955,48480,24242,12122,6061,3031,1515,758};


// 758 = 19
// 1515 = 18
// 3031 = 17
// 6061 = 16
// 12122 = 15
// 24242 = 14
// 48480 = 13
// 96955 = 12
// 193999 = 11
// 388357 = 10
// 777642 = 9
// 1530533 = 8
// 3067439 = 7
// 7107071 = 6
// 12407563 = 5
// 27301053 = 4
// 51957442 = 3

	private double scaleFactor;

	LengthUnit(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	/**
	 * Convert a value of this unit type to the units specified
	 * in the parameters.
	 *  
	 * @param toUnit the unit to convert to.
	 * @param value the value to convert.
	 * @return the converted value.
	 */
	public double convertTo(LengthUnit toUnit, double value) {
		double _value = value;
		if (this == PRIMARY) {
			if (toUnit == PRIMARY)
				return value; // Avoid multiplying by 1.0 needlessly.
		} else {
			_value = value / this.scaleFactor; // Convert to primary unit.
		}
		return _value * toUnit.scaleFactor; // Convert to destination unit.
	}

	/**
	 * Retrieve the scale factor between this unit and the primary 
	 * length unit.
	 * 
	 * @return the scale factor.
	 */
	public double getScaleFactor() {
		return scaleFactor;
	}
}