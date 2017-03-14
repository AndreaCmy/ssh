package main.java.com.cmy.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface BaseDao<VO> {
	    VO insertVo(VO var1) throws Exception;

	    ArrayList<VO> insertsVo(VO[] var1) throws Exception;

	    int updateVo(VO var1) throws Exception;

	    int updatesVo(VO[] var1) throws Exception;

//	    PageVo<VO> queryPage(String var1, VO var2, int var3, int var4) throws Exception;
//
//	    PageVo<VO> queryPage(VO var1, int var2, int var3) throws Exception;
//
//	    PageVo<VO> queryPageAll(String var1, VO var2, int var3, int var4) throws Exception;
//
//	    PageVo<VO> queryPageAll(VO var1, int var2, int var3) throws Exception;

	    List<VO> queryList(String var1, VO var2) throws Exception;

	    List<VO> queryList(VO var1) throws Exception;

	    long queryCount(VO var1) throws Exception;

	    List<VO> queryListAll(String var1, VO var2) throws Exception;

	    List<VO> queryListAll(VO var1) throws Exception;

	    long queryCountAll(VO var1) throws Exception;

	    VO queryOne(String var1, VO var2) throws Exception;

	    VO queryById(long var1) throws Exception;

	    VO queryOneAll(String var1, VO var2) throws Exception;

	    void deleteVo(VO var1) throws Exception;

	    void deleteVoNoById(VO var1, String[] var2) throws Exception;

	    void deletesVo(VO[] var1) throws Exception;

	    void deleteVoForEnumDataEntityStatus(VO var1) throws Exception;

	    int updateVoNotById(String var1, Map<String, Object> var2) throws Exception;
}
