package main.java.com.cmy.base;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dozer.DozerBeanMapper;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseDaoImpl<VO, Model> implements BaseDao<VO> {

	private Class<VO> VOClass;
    private Class<Model> ModelClass;
   /* @Autowired
    protected DynamicSqlTemplate dynamicSqlTemplate; 
    
    public BaseDaoImpl() throws Exception {
        Class tempC = this.getClass();
        if(!BaseDaoImpl.class.isAssignableFrom(tempC)) {
            throw new Exception("Dao继承关系不符合规范。");
        } else {
            while(!tempC.getSuperclass().equals(BaseDaoImpl.class)) {
                tempC = tempC.getSuperclass();
            }

            this.VOClass = (Class)((ParameterizedType)tempC.getGenericSuperclass()).getActualTypeArguments()[0];
            this.ModelClass = (Class)((ParameterizedType)tempC.getGenericSuperclass()).getActualTypeArguments()[1];
        }
    }

    public VO insertVo(VO vo) throws Exception {
        DozerBeanMapper mapper = new DozerBeanMapper();
        Object model = this.dynamicSqlTemplate.insert(vo, this.ModelClass);
        return mapper.map(model, this.VOClass);
    }

    public ArrayList<VO> insertsVo(VO[] vos) throws Exception {
        ArrayList list = new ArrayList();
        DozerBeanMapper mapper = new DozerBeanMapper();
        Object[] var4 = vos;
        int var5 = vos.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Object vo = var4[var6];
            Object model = this.dynamicSqlTemplate.insert(vo, this.ModelClass);
            list.add(mapper.map(model, this.VOClass));
        }

        return list;
    }

    public int updateVo(VO vo) throws Exception {
        return this.dynamicSqlTemplate.updateNonull(vo, this.ModelClass);
    }

    public int updatesVo(VO[] vos) throws Exception {
        int updateCount = 0;
        Object[] var3 = vos;
        int var4 = vos.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Object vo = var3[var5];
            updateCount += this.dynamicSqlTemplate.updateNonull(vo, this.ModelClass);
        }

        return updateCount;
    }

    public PageVo<VO> queryPage(String sqlName, VO vo, int page, int rows) throws Exception {
        return this.dynamicSqlTemplate.queryPage(sqlName, vo, page, rows, this.VOClass, false);
    }

    public PageVo<VO> queryPageAll(String sqlName, VO vo, int page, int rows) throws Exception {
        return this.dynamicSqlTemplate.queryPage(sqlName, vo, page, rows, this.VOClass, true);
    }

    public PageVo<VO> queryPage(VO vo, int page, int rows) throws Exception {
        return this.dynamicSqlTemplate.queryPage(vo, this.ModelClass, page, rows, 0L, false);
    }

    public PageVo<VO> queryPageAll(VO vo, int page, int rows) throws Exception {
        return this.dynamicSqlTemplate.queryPage(vo, this.ModelClass, page, rows, 0L, true);
    }

    public List<VO> queryList(String sqlName, VO vo) throws Exception {
        return this.dynamicSqlTemplate.queryList(sqlName, vo, this.VOClass, false);
    }

    public List<VO> queryListAll(String sqlName, VO vo) throws Exception {
        return this.dynamicSqlTemplate.queryList(sqlName, vo, this.VOClass, true);
    }

    public List<VO> queryList(VO vo) throws Exception {
        return this.dynamicSqlTemplate.queryPage(vo, this.ModelClass, 0, 0, 0L, false).getData();
    }

    public List<VO> queryListAll(VO vo) throws Exception {
        return this.dynamicSqlTemplate.queryPage(vo, this.ModelClass, 0, 0, 0L, true).getData();
    }

    public long queryCount(VO vo) throws Exception {
        return this.dynamicSqlTemplate.queryPage(vo, this.ModelClass, 0, 0, 0L, false).getTotalCount();
    }

    public long queryCountAll(VO vo) throws Exception {
        return this.dynamicSqlTemplate.queryPage(vo, this.ModelClass, 0, 0, 0L, true).getTotalCount();
    }

    public VO queryOne(String sqlName, VO vo) throws Exception {
        return this.dynamicSqlTemplate.queryOne(sqlName, vo, this.VOClass, false);
    }

    public VO queryOneAll(String sqlName, VO vo) throws Exception {
        return this.dynamicSqlTemplate.queryOne(sqlName, vo, this.VOClass, true);
    }

    public VO queryById(long id) throws Exception {
        PageVo vos = this.dynamicSqlTemplate.queryPage(this.VOClass.newInstance(), this.ModelClass, 0, 0, id, true);
        return vos != null && vos.getData() != null && vos.getData().size() >= 1?vos.getData().get(0):null;
    }

    public void deleteVo(VO vo) throws Exception {
        this.dynamicSqlTemplate.delete(vo, this.ModelClass);
    }

    public void deleteVoNoById(VO vo, String[] selectFieldNameArray) throws Exception {
        this.dynamicSqlTemplate.deleteNoById(vo, this.ModelClass, selectFieldNameArray);
    }

    public void deletesVo(VO[] vos) throws Exception {
        Object[] var2 = vos;
        int var3 = vos.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Object vo = var2[var4];
            this.dynamicSqlTemplate.delete(vo, this.ModelClass);
        }

    }

    public void deleteVoForEnumDataEntityStatus(VO vo) throws Exception {
        if(vo instanceof IDataEntity) {
            ((IDataEntity)vo).setEnumDataEntityStatus(Byte.valueOf(1));
            this.dynamicSqlTemplate.updateNonull(vo, this.ModelClass);
        } else {
            throw new Exception("deleteVoForEnumDataEntityStatus -> vo is not extends IDataEntity ");
        }
    }

    public int updateVoNotById(String sqlName, Map<String, Object> params) throws Exception {
        return this.dynamicSqlTemplate.updateSql(sqlName, params);
    }
    */
	@Override
	public VO insertVo(VO var1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ArrayList<VO> insertsVo(VO[] var1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int updateVo(VO var1) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int updatesVo(VO[] var1) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<VO> queryList(String var1, VO var2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<VO> queryList(VO var1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long queryCount(VO var1) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<VO> queryListAll(String var1, VO var2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<VO> queryListAll(VO var1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long queryCountAll(VO var1) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public VO queryOne(String var1, VO var2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public VO queryById(long var1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public VO queryOneAll(String var1, VO var2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void deleteVo(VO var1) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteVoNoById(VO var1, String[] var2) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deletesVo(VO[] var1) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteVoForEnumDataEntityStatus(VO var1) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int updateVoNotById(String var1, Map<String, Object> var2) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
    

}
