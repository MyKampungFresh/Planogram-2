package course.examples.helloandroid;

public class BarcodeData{

	private int[] codeDigits;
	private int nbDigits;
	private int checkDigit;

	private boolean isValid;

	private String code;
	private String symbology;

	public BarcodeData(String code){

		this.code = code;

		nbDigits = code.length();
		codeDigits = new int[nbDigits];

		for(int i = 0; i < nbDigits; i++){
			codeDigits[i] = Integer.parseInt(code.substring(i,i+1));
		}

		checkDigit = codeDigits[nbDigits-1];

		if(validate())
			isValid = true;
		else
			isValid = false;
	}

	public String getSymbology(){
		return symbology;
	}

	public String getBarcode(){
		return code;
	}

	public int[] getCodeDigits(){
		return codeDigits;
	}

	public boolean isValid(){

		return isValid;
	}

	// MC = Manufacturer Code
	public BarcodeData formatToUPCE(){

		boolean doesMCHave2digits = false;
		boolean doesMCHave3digits = false;
		boolean doesMCHave4digits = false;
		boolean doesMCHave5digits = false;

		String upcEEquiv;
		StringBuilder sb;
		BarcodeData upcE = new BarcodeData("00000000");

		if(this.getSymbology() == "UPC-A"){
			doesMCHave2digits = code.substring(3,6) == "000"
					|| code.substring(3,6) == "100"
					|| code.substring(3,6) == "200";
			doesMCHave3digits = code.substring(4,6) == "00";
			doesMCHave4digits = code.substring(5,6) == "0";
			doesMCHave5digits = codeDigits[10] >= 5;

			upcEEquiv = code.substring(0,3) + "0000" + code.substring(nbDigits-1);
			sb = new StringBuilder(upcEEquiv);

			if(doesMCHave2digits){
				sb.replace(3,6,code.substring(8,11));
				sb.replace(6,7,code.substring(3,4));
			}
			else if(doesMCHave3digits){
				sb.replace(3,4,code.substring(3,4));
				sb.replace(4,6,code.substring(9,11));
				sb.replace(6,7,"3");
			}
			else if(doesMCHave4digits){
				sb.replace(3,5,code.substring(3,5));
				sb.replace(5,6,code.substring(10,11));
				sb.replace(6,7,"4");
			}
			else if(doesMCHave5digits){
				sb.replace(3,6,code.substring(3,6));
				sb.replace(6,7,code.substring(10,11));
			}

			upcEEquiv = sb.toString();

			upcE = new BarcodeData(upcEEquiv);
		}

		return upcE;
	}

	public BarcodeData formatToUPCA(){

		int lastUPCEDigit = codeDigits[nbDigits-2];

		String upcAEquiv;

		BarcodeData upcA = new BarcodeData("000000000000");

		if(this.getSymbology() == "UPC-E"){
			upcAEquiv = code.substring(0,3) + "00000000"
						+ code.substring(nbDigits-1);

			StringBuilder sb = new StringBuilder(upcAEquiv);

			switch(lastUPCEDigit){
			case 0:
				sb.replace(8,11,code.substring(3,6));
				break;
			case 1:
				sb.replace(3,4,"1");
				sb.replace(8,11,code.substring(3,6));
				break;
			case 2:
				sb.replace(3,4,"2");
				sb.replace(8,11,code.substring(3,6));
				break;
			case 3:
				sb.replace(3,4,code.substring(3,4));
				sb.replace(9,11,code.substring(4,6));
				break;
			case 4:
				sb.replace(3,5,code.substring(3,5));
				sb.replace(10,11,code.substring(5,6));
				break;
			case 5:
				sb.replace(3,6,code.substring(3,6));
				sb.replace(10,11,"5");
				break;
			case 6:
				sb.replace(3,6,code.substring(3,6));
				sb.replace(10,11,"6");
				break;
			case 7:
				sb.replace(3,6,code.substring(3,6));
				sb.replace(10,11,"7");
				break;
			case 8:
				sb.replace(3,6,code.substring(3,6));
				sb.replace(10,11,"8");
				break;
			case 9:
				sb.replace(3,6,code.substring(3,6));
				sb.replace(10,11,"9");
				break;
			}

			upcAEquiv = sb.toString();

			upcA = new BarcodeData(upcAEquiv);
		}

		return upcA;
	}

	private boolean validate(){

		if(validateUPCA() || validateUPCE() || validateEAN13())
			return true;
		else
			return false;
	}

	private boolean validateUPCA(){
		int i;
		int evenSum = 0;
		int oddSum = 0;
		int verifDigit = 0;

		boolean isUPCA = false;

		if(codeDigits.length == 12){
			i = 0;
			while(i < codeDigits.length - 1){
				evenSum += codeDigits[i];
				i = i + 2;
			}

			i = 1;
			while(i < codeDigits.length - 1){
				oddSum += codeDigits[i];
				i = i + 2;
			}

			evenSum = 7 * evenSum;
			oddSum = 9 * oddSum;

			verifDigit = (evenSum + oddSum) % 10;

			if(verifDigit == checkDigit){
				isUPCA = true;
				symbology = "UPC-A";
			}
		}

		return isUPCA;
	}

	private boolean validateUPCE(){

		boolean isUPCE = false;

		if(nbDigits == 8 && (codeDigits[0] == 0 || codeDigits[0] == 1)){

			BarcodeData bc;
			bc = this.formatToUPCA();

			if(bc.validateUPCA()){
				symbology = "UPC-E";
				isUPCE = true;
			}
		}

		return isUPCE;
	}

	private boolean validateEAN13(){

		int i;
		int eanSum = 0;
		int verifDigit = 0;
		int weight;
		boolean isEAN13 = false;

		if(nbDigits == 13){
			for(i = 0; i < nbDigits-1; i++){
				if(i % 2 == 0)
					weight = 1;
				else
					weight = 3;

				eanSum += weight * codeDigits[i];
			}

			verifDigit = 10 - (eanSum % 10);

			if(verifDigit == checkDigit){
				symbology = "EAN-13";
				isEAN13 = true;
			}
		}

		return isEAN13;
	}

}