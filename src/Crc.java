/*
@authors: Vladut Madalin Druta
		Antonio Nikolova
		Mark Whelan
*/
public class Crc {
	
	
	
	private String divisor = "100000111"; // set the divisor accordingly to CRC-8 
	
	/**
	 * contructor: create the empty crc
	 */
	public Crc(){
		
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public byte generateCRCbyte(byte[] data){
		byte crcResult = 0;
			
		String dataString = convertByteArrayToString(data); // save the string
		
		int reps = dataString.length()-divisor.length();
		for(int i=0;i<reps;i++)
			divisor = divisor + "0";
		
		byte[] divisorByteArray = convertStringToByteArray(divisor);
		
		String print="";
		while(dataString.length()>8){
			
			byte[] result = new byte[data.length];
			for(int i=0;i<data.length;i++)
				result[i]=(byte) (data[i]^divisorByteArray[i]); // xOR every byte
			
			dataString = convertByteArrayToString(data);
			
			
		}
		
		// <-- part to get the exceptions where the result out of the error when the result 
				
		
		return crcResult;
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public boolean checkCRConData(byte[] data){
		boolean result = false;
		
		return result;
	}
	
/**
 * 
 * @param data
 * @return
 */
    private String convertByteArrayToString(byte[] data) {
        String result = new String(data);
        System.out.println(result);
        return result;
    }
    /**
     * 
     * @param data
     * @return
     */
    private byte[] convertStringToByteArray(String data){
    	byte[] result = data.getBytes();
    	return result;
    }
	
	
//	public byte generateCRCbyte(byte[] data){
//		byte result;
//		
//		return result;
//	}
}
