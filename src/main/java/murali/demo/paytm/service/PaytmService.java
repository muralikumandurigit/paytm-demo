package murali.demo.paytm.service;

import java.util.TreeMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.paytm.pg.merchant.PaytmChecksum;

import lombok.extern.slf4j.Slf4j;
import murali.demo.paytm.entity.Paytm;

@Service
@Slf4j
public class PaytmService {

	@Autowired
	private Paytm paytm;
	
	@Autowired
	private Environment env;
	
	public String getRedirectUrl() {
		return paytm.getPaytmUrl();
	}

	public TreeMap<String, String> getPaytmParameters(String amount) throws Exception {
		TreeMap<String, String> parameters = new TreeMap<>();
		paytm.getDetails().forEach((K, V) -> parameters.put(K, V));
		parameters.put("MOBILE_NO", env.getProperty("paytm.my.mobile"));
		parameters.put("EMAIL", env.getProperty("paytm.my.email"));
		parameters.put("ORDER_ID", UUID.randomUUID().toString());
		parameters.put("TXN_AMOUNT", amount);
		parameters.put("CUST_ID", UUID.randomUUID().toString());
		log.info("parameters = " + parameters.toString());
		log.info("merchant id = " + paytm.getMerchantId());
		String checkSum = PaytmChecksum.generateSignature(parameters, paytm.getMerchantKey());
		log.info("Checsum = " + checkSum);
		parameters.put("CHECKSUMHASH", checkSum);
		return parameters;
	}

	public String validatePayment(TreeMap<String, String> parameters) throws Exception {
		String checkSum = parameters.remove("CHECKSUMHASH");
		
		if (!PaytmChecksum.verifySignature(parameters, paytm.getMerchantKey(), checkSum)) {
			return "Checksum Validation Failed";
		}
		if (parameters.containsKey("RESPCODE")) {
			if("01".equals(parameters.get("RESPCODE"))) {
				return "Payment Successful";
			}
			else {
				return "Payment Failed!!!";
			}
		}
		else {
			return "No response code from paytm";
		}
	}
}
