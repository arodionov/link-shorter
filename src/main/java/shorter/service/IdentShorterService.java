package shorter.service;

import ioc.annotations.PostConstructBean;

public class IdentShorterService implements ShorterService {

	@Override
	public String shorten(String path) {
		return path;
	}

	@PostConstructBean
	public void init(){
		System.out.println("Method init in ShorterService");
	}
}
