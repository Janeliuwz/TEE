package com.tee686.im;

import java.util.Comparator;

public class PinyinComparator implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		 String str1 = PinyinUtil.getPingYin((String) o1);
	     String str2 = PinyinUtil.getPingYin((String) o2);
	     return str1.compareToIgnoreCase(str2);
	}

}
