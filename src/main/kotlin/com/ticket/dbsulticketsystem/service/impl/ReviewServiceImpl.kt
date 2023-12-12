package com.ticket.dbsulticketsystem.service.impl

import com.ticket.dbsulticketsystem.domain.Review
import com.ticket.dbsulticketsystem.repository.ReviewRepository
import com.ticket.dbsulticketsystem.service.ReviewService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class ReviewServiceImpl(
    private val reviewRepository: ReviewRepository,
): ReviewService {
    override fun getReviewList(goodsId: Int): List<Review> {
        return reviewRepository.findAllByGoodsId(goodsId)
    }

    override fun writeReview(goodsId: Int, title: String, content: String, score: Int, userId: Int) {
        val review = Review(
            userId = userId,
            goodsId = goodsId,
            title = title,
            content = content,
            score = score,
        )
        reviewRepository.save(review)
    }

    override fun getReview(id: Int): Review {
        return reviewRepository.findById(id).orElseThrow { NoSuchElementException() }
    }
}