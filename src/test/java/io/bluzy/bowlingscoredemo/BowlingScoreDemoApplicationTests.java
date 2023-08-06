/**  Copyright 2023 Florin Potera
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package io.bluzy.bowlingscoredemo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

import static io.bluzy.bowlingscoredemo.BowlingCounterService.*;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(MyTestConfiguration.class)
class BowlingScoreDemoApplicationTests {

	public static final byte NR_TEST_FRAMES = 5;

	@Autowired
	BowlingCounterService bowlingCounterService;

	@Test
	void testFirst4FrameRollMethodWithoutStrikeAndSpare() {
		checkCurrent(3,  0, FR, 3,  0, SR);
		checkCurrent(6,  0, SR, 9,  1, FR);
		checkCurrent(5,  1, FR, 14,  1, SR);
		checkCurrent(1,  1, SR, 15,  2, FR);
		checkCurrent(0,  2, FR, 15,  2, SR);
		checkCurrent(3,  2, SR, 18,  3, FR);
		checkCurrent(4,  3, FR, 22,  3, SR);
		checkCurrent(0,  3, SR, 22,  4, FR);
	}

	@Test
	void testFirst4FrameRollMethodWithStrike() {
		checkCurrent(STRIKE,  0, FR, STRIKE,  1, FR);
		checkCurrentPlusPreview(2, 1, FR, 14, 1, SR, 12);
		checkCurrentPlusPreview(1, 1, SR, 16, 2, FR, 13);
		checkCurrentPlusPreview(STRIKE, 2, FR, 26, 3, FR, 16);
		checkCurrentPlusMorePreview(5, 3, FR, 36, 3, SR, 31, 16);
		checkCurrentPlusMorePreview(3, 3, SR, 42, 4, FR, 34, 16);
	}

	@Test
	void testFirst4FrameRollMethodWithSpare() {
		checkCurrent(3,  0, FR, 3,  0, SR);
		checkCurrent(7,  0, SR, 10,  1, FR);
		checkCurrentPlusPreview(2, 1, FR, 14, 1, SR, 12);
		checkCurrentPlusPreview(1, 1, SR, 15, 2, FR, 12);
		checkCurrentPlusMorePreview(8, 2, FR, 23, 2, SR, 15, 12);
		checkCurrentPlusMorePreview(2, 2, SR, 25, 3, FR, 15, 12);
		checkCurrentPlusMorePreview(0, 3, FR, 25, 3, SR, 25, 15);
		checkCurrentPlusMorePreview(2, 3, SR, 27, 4, FR, 25, 15);
	}

	@Test
	void testRollMethodWithAllStrike() {
		checkCurrent(STRIKE,  0, FR, STRIKE,  1, FR);
		checkCurrentPlusPreview(STRIKE, 1, FR, 30, 2, FR, 20);
		checkCurrentPlusMorePreview(STRIKE, 2, FR, 60, 3, FR, 50, 30);
		checkCurrentPlusMorePreview(STRIKE, 3, FR, 90, 4, FR, 80, 60);
		checkCurrentPlusMorePreview(STRIKE, 4, FR, 120, 5, FR, 110, 90);
	}

	@Test
	void testRollMethodWithAllSpare() {
		checkCurrent(3,  0, FR, 3,  0, SR);
		checkCurrent(7,  0, SR, 10,  1, FR);
		checkCurrentPlusPreview(5, 1, FR, 20, 1, SR, 15);
		checkCurrentPlusPreview(5, 1, SR, 25, 2, FR, 15);
		checkCurrentPlusMorePreview(5, 2, FR, 35, 2, SR, 30, 15);
		checkCurrentPlusMorePreview(5, 2, SR, 40, 3, FR, 30, 15);
		checkCurrentPlusMorePreview(5, 3, FR, 50, 3, SR, 45, 30);
		checkCurrentPlusMorePreview(5, 3, SR, 55, 4, FR, 45, 30);
		checkCurrentPlusMorePreview(5, 4, FR, 65, 4, SR, 60, 45);
		checkCurrentPlusMorePreview(5, 4, SR, 70, 5, FR, 60, 45);
	}

	@Test
	void testRollMethodWithStrikeAndSpare() {
		checkCurrent(STRIKE,  0, FR, STRIKE,  1, FR);
		checkCurrentPlusPreview(5, 1, FR, 20, 1, SR, 15);
		checkCurrentPlusPreview(5, 1, SR, 30, 2, FR, 20);
		checkCurrentPlusMorePreview(STRIKE, 2, FR, 50, 3, FR, 40, 20);
		checkCurrentPlusMorePreview(5, 3, FR, 60, 3, SR, 55, 40);
		checkCurrentPlusMorePreview(5, 3, SR, 70, 4, FR, 60, 40);
	}

	@BeforeEach
	void setBeforeEveryTest() {
		bowlingCounterService.setFrames(buildBlankFrames(), (byte) 0, (byte) 0);
	}

	private short[][] buildBlankFrames() {
		short[][] frames = new short[NR_TEST_FRAMES][4];
		for(var frame:frames) {
			Arrays.fill(frame, (short) -1);
		}
		return frames;
	}

	private void checkCurrent(int pins, int crtFrame, int crtRoll, int points, int newFrame, int newRoll) {
		out.format("Check pins: %d, frame: %d, roll: %d, points: %d, newFrame: %d, newRoll: %d\n",
				pins, crtFrame, crtRoll, points, newFrame, newRoll);

		assertTrue(bowlingCounterService.roll((byte) pins), "The play is not finished.");
		assertEquals(newFrame, bowlingCounterService.getCrtFrame(), "Is on the "+newFrame+" frame.");
		assertEquals(newRoll, bowlingCounterService.getCrtRoll(), "Is on the "+newRoll+" roll.");
		short[][] frames = bowlingCounterService.getFrames();
		out.format("Frames: %s, crtFrame: %d, crtRoll: %d\n",
				Arrays.deepToString(bowlingCounterService.getFrames()),
				bowlingCounterService.getCrtFrame(), bowlingCounterService.getCrtRoll());
		assertEquals(points, frames[crtFrame][PT], points+" points for this roll");
		assertEquals(pins, frames[crtFrame][crtRoll], points+" pins for this roll");
	}

	private void checkCurrentPlusPreview(int pins, int crtFrame, int crtRoll, int points, int newFrame, int newRoll,
						  int previewPoints) {
		checkCurrent(pins, crtFrame, crtRoll, points, newFrame, newRoll);
		short[][] frames = bowlingCounterService.getFrames();
		assertEquals(previewPoints, frames[crtFrame-1][PT], previewPoints+" points for preview frame");
	}

	private void checkCurrentPlusMorePreview(int pins, int crtFrame, int crtRoll, int points, int newFrame, int newRoll,
										 int previewPoints, int previewMorePoints) {
		checkCurrentPlusPreview(pins, crtFrame, crtRoll, points, newFrame, newRoll, previewPoints);
		short[][] frames = bowlingCounterService.getFrames();
		assertEquals(previewMorePoints, frames[crtFrame-2][PT], previewMorePoints+" points for more preview frame");
	}
}

@TestConfiguration
class MyTestConfiguration {
	BowlingCounterService buildCounter() {
		return new BowlingCounterService(BowlingScoreDemoApplicationTests.NR_TEST_FRAMES);
	}
}