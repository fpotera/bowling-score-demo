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

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class BowlingCounterService {
    private short[][] frames;

    static final byte FR = 0;
    static final byte SR = 1;
    static final byte TR = 2;
    static final byte PT = 3;
    static final byte STRIKE = 10;
    static final byte NR_FRAMES = 10;
    private byte crtFrame;
    private byte crtRoll;

    private final byte noOfFrames;

    public BowlingCounterService() {
        this(NR_FRAMES);
    }

    public BowlingCounterService(byte noOfFrames) {
        this.noOfFrames = noOfFrames;
        resetGame();
    }

    short[][] getFrames() {
        return frames;
    }

    byte getCrtFrame() {
        return crtFrame;
    }

    byte getCrtRoll() {
        return crtRoll;
    }

    void setFrames(short[][] frames, byte crtFrame, byte crtRoll) {
        this.frames = frames;
        this.crtFrame = crtFrame;
        this.crtRoll = crtRoll;
    }

    public void resetGame() {
        frames = new short[noOfFrames][4];
        for(var frame:frames) {
            Arrays.fill(frame, (short) -1);
        }
        crtFrame = 0;
        crtRoll = FR;
    }

    public boolean roll(byte pins) {
        frames[crtFrame][crtRoll] = pins;
        if(crtFrame > 0) {
            addPinsToPreviewsFrames(pins);
            frames[crtFrame][PT] = frames[crtFrame-1][PT];
            frames[crtFrame][PT] += pins;
            if(crtRoll > FR) {
                frames[crtFrame][PT] += frames[crtFrame][FR];
                if(crtRoll > SR) {
                    frames[crtFrame][PT] += frames[crtFrame][SR];
                }
            }
        }
        else if(crtRoll == FR){
            frames[crtFrame][PT] = pins;
        }
        else {
            frames[crtFrame][PT] += pins;
        }

        return prepareNextRoll(pins);
    }

    private boolean prepareNextRoll(byte pins) {
        if(pins==STRIKE) {
            if(++crtFrame >= noOfFrames) {
                return false;
            }
        }
        else if(crtRoll == FR){
            crtRoll = SR;
        }
        else if(crtRoll == SR && crtFrame == noOfFrames-1) {
            crtRoll = TR;
        }
        else {
            crtRoll = FR;
            if(++crtFrame >= noOfFrames) {
                return false;
            }
        }
        return true;
    }

    public boolean isPinCountValid(byte pins) {
        if(pins < 0 || pins > STRIKE) {
            return false;
        }
        if(crtRoll == SR && (frames[crtFrame][FR] + pins > STRIKE)) {
            return false;
        }
        if(crtRoll == TR && (frames[crtFrame][FR] + frames[crtFrame][SR] + pins > STRIKE)) {
            return false;
        }
        return true;
    }

    public boolean isGameFinished() {
        return crtFrame >= noOfFrames;
    }

    private void addPinsToPreviewsFrames(byte pins) {
        byte frame = crtFrame;
        if(--frame >=0) {
            if(crtRoll==FR){
                if(isFrameStrike(frame) || isFrameSpare(frame)) {
                    frames[frame][PT] += pins;
                }
                if(isFrameStrike(frame) && (--frame >= 0) && isFrameStrike(frame)) {
                    frames[frame][PT] += pins;
                    frames[++frame][PT] += pins;
                }
            }
            else if(isFrameStrike(frame)) {
                frames[frame][PT] += pins;
            }
        }
    }

    private boolean isFrameStrike(byte frame) {
        return frames[frame][FR] == STRIKE;
    }

    private boolean isFrameSpare(byte frame) {
        return !isFrameStrike(frame) && (frames[frame][FR]+frames[frame][SR] == STRIKE);
    }
}
