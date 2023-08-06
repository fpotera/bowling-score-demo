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

import jakarta.faces.view.ViewScoped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static io.bluzy.bowlingscoredemo.BowlingCounterService.*;

@Component
@ViewScoped
public class BowlingCounterBean {

    public static final String SPACE = "_";
    @Autowired
    BowlingCounterService bowlingCounter;

    public void doRoll(byte pins) {
        if(!bowlingCounter.isGameFinished() && bowlingCounter.isPinCountValid(pins))
            bowlingCounter.roll(pins);
    }

    public String[][] buildScore() {
        String[][] score = new String[NR_FRAMES][4];
        for(var frame: score) {
            Arrays.fill(frame, SPACE);
        }
        int i = 0;
        for(var frame: bowlingCounter.getFrames()) {
            int j = 0;
            for(var elem: frame) {
                if(elem != -1) {
                    score[i][j] = String.valueOf(elem);
                }
                j++;
            }
            if(frame[FR] == STRIKE) {
                score[i][FR] = "X";
            }
            else if(frame[FR] + frame[SR] == STRIKE) {
                score[i][FR] = "/";
            }
            if(frame[FR] == 0) {
                score[i][FR] = "-";
            }
            if(frame[SR] == 0) {
                score[i][SR] = "-";
            }
            if(frame[TR] == 0) {
                score[i][TR] = "-";
            }
            i++;
        }
        return score;
    }

    public boolean isGameFinished() {
        return bowlingCounter.isGameFinished();
    }

    public void resetGame() {
        bowlingCounter.resetGame();
    }
}
