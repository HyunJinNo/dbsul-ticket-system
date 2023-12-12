package com.ticket.dbsulticketsystem.service.impl

import com.ticket.dbsulticketsystem.domain.Goods
import com.ticket.dbsulticketsystem.domain.Sequence
import com.ticket.dbsulticketsystem.repository.GoodsRepository
import com.ticket.dbsulticketsystem.repository.LikeRepository
import com.ticket.dbsulticketsystem.repository.SequenceRepository
import com.ticket.dbsulticketsystem.service.GoodsService
import com.ticket.dbsulticketsystem.service.dto.GoodsInfo
import com.ticket.dbsulticketsystem.service.dto.SequenceDto
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GoodsServiceImpl(
    private val goodsRepository: GoodsRepository,
    private val likeRepository: LikeRepository,
    private val sequenceRepository: SequenceRepository,
) : GoodsService {

    override fun getGoodsList(genreId: Int): List<GoodsInfo.GoodsSimpleDto> {
        val goods = goodsRepository.findAllByGenreId(genreId)
        val dto = goods.map {
            GoodsInfo.GoodsSimpleDto(
                id = it.id,
                title = it.title,
                startDate = it.startDate,
                endDate = it.endDate,
                genreName = it.genre?.genreName,
                goodsImageUrl = it.goodsImageUrl,
                placeName = it.place?.name,
            )
        }
        return dto
    }

    private fun makeGoodsDto(
        it: Goods,
        sequence: List<Sequence>,
    ) = GoodsInfo.GoodsDto(
        id = it.id,
        title = it.title,
        placeId = it.place?.id,
        placeName = it.place?.name,
        startDate = it.startDate,
        endDate = it.endDate,
        genreName = it.genre?.genreName,
        viewRate = it.viewRate,
        goodsImageUrl = it.goodsImageUrl,
        goodsInfo = it.goodsInfo,
        salesInfo = it.salesInfo,
        runningTime = it.runningTime,
        interMissionTime = it.interMissionTime,
        ticketOpenDate = it.ticketOpenDate,
        bookingEndDate = it.bookingEndDate,
        maxBooking = it.maxBooking,
        createdAt = it.createdAt,
        updatedAt = it.updatedAt,
        sequenceList = sequence.map { s ->
            SequenceDto(
                id = s.id,
                date = s.date,
                time = s.time,
                isFree = s.isFree,
                createdAt = s.createdAt,
                updatedAt = s.updatedAt,
            )
        }
    )

    override fun getGoods(goodsId: Int): GoodsInfo.GoodsDto {
        val goods = goodsRepository.findById(goodsId).orElseThrow { NoSuchElementException() }
        val sequence = sequenceRepository.findAllByGoodsId(goodsId)
        return makeGoodsDto(goods, sequence)
    }

    override fun getPopularGoodsList(genreId: Int): List<GoodsInfo.GoodsSimpleDto> {
        val goodsList =
            goodsRepository.findAllByGenreIdAndBookingEndDateLessThan(genreId, LocalDate.now())
        val goodsIds = goodsList.map { it.id }
        val goodsIdToLikeCount = likeRepository.findAllByGoodsIdIn(goodsIds).groupBy { it.goods.id }
            .mapValues { it.value.size }

        return goodsIds.map { goodsIdToLikeCount[it] to goodsList.first { goods -> goods.id == it } }
            .sortedByDescending { it.first }
            .map { it.second }
            .map {
                GoodsInfo.GoodsSimpleDto(
                    id = it.id,
                    title = it.title,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    genreName = it.genre?.genreName,
                    goodsImageUrl = it.goodsImageUrl,
                    placeName = it.place?.name,
                )
            }
    }
}