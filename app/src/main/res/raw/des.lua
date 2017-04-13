key = "19809172";


function doDec(ciphertxt, len)
	dc = luajava.newInstance("com.decapi.Decryptions");
	ret = dc:DESDec(ciphertxt, len, key);
	return ret;
end

function doEnc(plaintxt)
	ec = luajava.newInstance("com.decapi.Decryptions");
	ret = ec:DESEnc(plaintxt, key);
	return ret;
end