package murali.demo.paytm.controller;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import murali.demo.paytm.service.PaytmService;

@Controller
//@RequestMapping("/payment")
public class PaytmController {

	@Autowired
	private PaytmService paytmService;
	
	@GetMapping("/")
	public String home() {
		return "home";
	}
	
	@PostMapping("/pgredirect")
	public ModelAndView pgRedirect(@RequestParam String amount ) throws Exception {
		ModelAndView modelAndView = new ModelAndView("redirect:" + paytmService.getRedirectUrl());
		TreeMap<String, String> parameters = paytmService.getPaytmParameters(amount);
		return modelAndView.addAllObjects(parameters);
	}
	
	@PostMapping("/pgresponse")
	public String pgResponseRedirect(HttpServletRequest request, Model model) throws Exception {
		/*
		TreeMap<String, String> parameters = request.getParameterMap()
			                                        .entrySet()
			                                        .stream()
			                                        .filter(e -> !e.getKey().equals("CHECKSUMHASH"))
			                                        .collect(Collectors.toMap(Map.Entry::getKey, 
			    		                                                      Map.Entry::getValue, 
			    		                                                      (oldValue, newValue) -> newValue, 
			    		                                                      TreeMap::new));
			    		                                                      */
		TreeMap<String, String> parameters = new TreeMap<>();
		request.getParameterMap().forEach((K,V) -> parameters.put(K, V[0]));
		String paymentStatusMessage = paytmService.validatePayment(parameters);
		model.addAttribute("result", paymentStatusMessage);
		model.addAllAttributes(parameters);
		return "";
	}
}
