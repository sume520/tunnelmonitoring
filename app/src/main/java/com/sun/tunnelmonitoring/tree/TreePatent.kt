package com.sun.tunnelmonitoring.tree

/**
 * Created by ZBL on 2018/12/12.
 */

data class TreePatent
/**
 *
 * @param id
 * @param parentId
 * @param name
 */
    (
    var id: Int//对应节点的groupId
    , var parentId: Int//父类编号
    , var name: String?//名称
)
