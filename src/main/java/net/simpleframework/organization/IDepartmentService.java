package net.simpleframework.organization;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ctx.service.ado.ITreeBeanServiceAware;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IDepartmentService extends IDbBeanService<Department>,
		ITreeBeanServiceAware<Department> {

	/**
	 * 
	 * @param parent
	 * @param departmentType
	 * @param orderColumns
	 * @return
	 */
	IDataQuery<Department> queryChildren(Department parent, EDepartmentType departmentType,
			ColumnData... orderColumns);
}
