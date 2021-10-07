package com.example.controledeestoque.helpers

import com.example.controledeestoque.divergencia.DivergenciasPendentes
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement


class GraficoResumo {

    fun dataGrafico(data: MutableList<DivergenciasPendentes>): Array<Any> {
        val dataArry = arrayListOf<Any>()
        for (i in 0 until data.size) {
            val novoArry = arrayOf(data[i].categoria, data[i].qtdProdutos)
            dataArry.add(novoArry)
        }
        return dataArry.toArray()
    }


    fun graficoPizza(data: MutableList<DivergenciasPendentes>): AAChartModel {

        val dataDiv = dataGrafico(data)

        val graficoPizza = AAChartModel()
            .chartType(AAChartType.Pie)
            .backgroundColor("#F8F8FA")
            .dataLabelsEnabled(true)
            .legendEnabled(false)
            .series(arrayOf(
                AASeriesElement()
                    .name("Prod.")
                    .borderWidth(0f)
                    .lineWidth(0f)
                    .innerSize("60%")
                    .size("60%")
                    .data(dataDiv
                    )))
        return graficoPizza
    }
}