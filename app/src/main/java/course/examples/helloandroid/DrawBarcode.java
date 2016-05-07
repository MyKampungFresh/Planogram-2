package course.examples.helloandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Arrays;

public class DrawBarcode extends View {
    Paint paint = new Paint();

    private static String guardBar = "(101)";
    private static String centerBar = "(01010)";

    private static int[] leftOddPattern = {13,25,19,61,35,49,47,59,55,11};
    private static int[] leftEvenPattern = {39,51,27,33,29,57,5,17,9,23};
    private static int[] rightEvenPattern = {114,102,108,66,92,78,80,68,72,116};

    private static int[] upceNbSys0Enc = {56,52,50,49,44,38,35,42,41,37};
    private static int[] upceNbSys1Enc = {7,11,13,14,19,25,28,21,22,26};
    private static int[] eanParity = {0,11,13,14,19,25,28,21,22,26};

    float startXPix = 0;
    float topYPix = 0;
    float bottomYPix;
    float minSize = 400;
    float scaling = 5;

    String encoding;
    String code;


    public DrawBarcode(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int[] bcDigits = {7,9,9,3,6,6,6,2,9,7,0,2};
        code = "799366629702";

        encodeUPCA(bcDigits);
        drawBars(canvas);
    }

    private void encodeUPCA(int[] bcDigits){

        encoding = guardBar;
        for(int i = 0; i < code.length(); i++){
            if(i < 6)
                encoding += decToBinString(leftOddPattern[bcDigits[i]],7);
            else if(i == 6){
                encoding += centerBar;
                encoding += decToBinString(rightEvenPattern[bcDigits[i]],7);
            }
            else if(i > 6 && i < code.length())
                encoding += decToBinString(rightEvenPattern[bcDigits[i]],7);
        }
        encoding += guardBar;
    }

    private void encodeUPCE(int[] bcDigits){
        int numberSystem = Integer.parseInt(code.substring(0,1));
        int checkDigit = Integer.parseInt(code.substring(code.length()-1));

        String parityPattern = "";

        if(numberSystem == 0)
            parityPattern = decToBinString(upceNbSys0Enc[checkDigit],6);
        else if(numberSystem == 1)
            parityPattern = decToBinString(upceNbSys1Enc[checkDigit],6);

        encoding = guardBar;
        for(int i = 1; i < code.length()-1; i++){
            if(parityPattern.charAt(i-1) == '1'){
                encoding += decToBinString(leftEvenPattern[bcDigits[i]],7);
            }
            else if(parityPattern.charAt(i-1) == '0')
                encoding += decToBinString(leftOddPattern[bcDigits[i]],7);
        }
        encoding += centerBar;
        encoding += "(1)";
    }

    private void encodeEAN13(int[] bcDigits){
        int numberSystem = Integer.parseInt(code.substring(0,1));

        String eanParityEnc = decToBinString(eanParity[numberSystem],6);
        System.out.println(eanParityEnc);

        encoding = guardBar;
        for(int i = 1; i < code.length(); i++){
            if(i < 7){
                if(eanParityEnc.charAt(i-1) == '1')
                    encoding += decToBinString(leftEvenPattern[bcDigits[i]],7);
                else if(eanParityEnc.charAt(i-1) == '0')
                    encoding += decToBinString(leftOddPattern[bcDigits[i]],7);
            }
            else if(i == 7){
                encoding += centerBar;
                encoding += decToBinString(rightEvenPattern[bcDigits[i]],7);
            }
            else if(i > 7 && i < code.length())
                encoding += decToBinString(rightEvenPattern[bcDigits[i]],7);
        }
        encoding += guardBar;
    }

    private void drawBars(Canvas canvas){

        float pix = startXPix;

        bottomYPix = topYPix + minSize;

        paint.setStrokeWidth(1.25F * scaling);

        for(int i = 0; i < encoding.length(); i++)
        {
            if(encoding.charAt(i) == '1'){
                canvas.drawLine(pix, topYPix, pix, bottomYPix, paint);
                pix = pix + scaling;
            }
            else if(encoding.charAt(i) == '0')
                pix = pix + scaling;
        }
    }

    private String decToBinString(int decNumber, int totalLength){
        String binString;

        binString = Integer.toBinaryString(decNumber);
        int length = totalLength - binString.length();
        char[] padArray = new char[length];
        Arrays.fill(padArray, '0');
        String padString = new String(padArray);
        binString = padString + binString;

        return binString;
    }

}
