package com.hkm.flixhub.utils

import androidx.paging.PagedList
import io.mockk.every
import io.mockk.mockk

object PagedListUtil {
    fun <T> mockKPagedList(list: List<T>): PagedList<T> {
        val pagedList = mockk<PagedList<T>>(relaxed = true)
        every { pagedList[anyIntVararg()[0]] } answers { call ->
            val index = call.invocation.args.first() as Int
            list[index]
        }

        every { pagedList.size } returns list.size

        return pagedList
    }
}