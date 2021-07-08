/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.dcp2lo.dcp;

public final class CodeConverter {

	private CodeConverter() {}
	
	public static String iccidToNsce(String iccid) {		
		String part = iccid.substring(6,  18);
		String checkDigit = calculateLuhnCheckDigit(part);
		return part + checkDigit;
	}
	
	// https://www.admfactory.com/luhn-algorithm-implementation-in-java/
	private static String calculateLuhnCheckDigit(String card) {
		if (card == null)
			return null;
		String digit;
		/* convert to array of int for simplicity */
		int[] digits = new int[card.length()];
		for (int i = 0; i < card.length(); i++) {
			digits[i] = Character.getNumericValue(card.charAt(i));
		}
		
		/* double every other starting from right - jumping from 2 in 2 */
		for (int i = digits.length - 1; i >= 0; i -= 2)	{
			digits[i] += digits[i];
			
			/* taking the sum of digits grater than 10 - simple trick by substract 9 */
			if (digits[i] >= 10) {
				digits[i] = digits[i] - 9;
			}
		}
		int sum = 0;
		for (int i = 0; i < digits.length; i++) {
			sum += digits[i];
		}
		/* multiply by 9 step */
		sum = sum * 9;
		/* convert to string to be easier to take the last digit */
		digit = sum + "";
		return digit.substring(digit.length() - 1);
	}
}