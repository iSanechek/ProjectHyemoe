package com.list.rados.fastlist

import android.view.View

class BindMap<T>(val layout: Int, var type: Int = 0, val bind: View.(item: T) -> Unit, val predicate: (item: T) -> Boolean)