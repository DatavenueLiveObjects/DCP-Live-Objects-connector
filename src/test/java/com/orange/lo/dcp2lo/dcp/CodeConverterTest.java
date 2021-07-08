package com.orange.lo.dcp2lo.dcp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CodeConverterTest {

	@ParameterizedTest
	@MethodSource("provideIccidToNsce")
	void shouldComputeValidNsce(String input, String expected) {		
		// when
		String nsce = CodeConverter.iccidToNsce(input);
		
		// then
		assertEquals(expected, nsce);
	}
	
	private static Stream<Arguments> provideIccidToNsce() {
		return Stream.of(
			Arguments.of("89320260542013166380","6054201316636"),
			Arguments.of("89320260542013166460","6054201316644"),
			Arguments.of("89320260542013166530","6054201316651"),
			Arguments.of("89320260542013166610","6054201316669"),
			Arguments.of("89320260542013166790","6054201316677")
		);
	}
}
