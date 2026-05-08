const { Router } = require('express');
const prisma = require('../lib/prisma');
const { AppError } = require('../middleware/errorHandler');
const { recordAudit } = require('../services/auditLog');
const {
  creditDriverAdjustmentTx,
  debitBookingAdjustmentTx,
} = require('../services/walletLedger');

const router = Router();

router.get('/', async (req, res, next) => {
  try {
    const status = String(req.query.status || 'PENDING').trim().toUpperCase();
    const charges = await prisma.bookingExtraCharge.findMany({
      where: status === 'ALL' ? {} : { status },
      include: {
        booking: {
          select: {
            id: true,
            orderCode: true,
            userId: true,
            status: true,
            pickupAddress: true,
            deliveryAddress: true,
          },
        },
        driver: { select: { id: true, name: true, phone: true } },
      },
      orderBy: { createdAt: 'desc' },
      take: 100,
    });
    res.json({ success: true, data: charges });
  } catch (err) {
    next(err);
  }
});

router.post('/:id/review', async (req, res, next) => {
  try {
    const decision = String(req.body?.decision || '').trim().toUpperCase();
    const rejectionReason = String(req.body?.reason || '').trim();
    if (!['APPROVED', 'REJECTED'].includes(decision)) {
      return next(new AppError('decision must be APPROVED or REJECTED', 400));
    }

    const charge = await prisma.bookingExtraCharge.findUnique({
      where: { id: req.params.id },
      include: { booking: true },
    });
    if (!charge) return next(new AppError('Extra charge not found', 404));
    if (charge.status !== 'PENDING') {
      return next(new AppError('Extra charge has already been reviewed', 400));
    }

    const updated = await prisma.$transaction(async (tx) => {
      const reviewed = await tx.bookingExtraCharge.update({
        where: { id: charge.id },
        data: {
          status: decision,
          reviewedByAdminId: 'ADMIN',
          reviewedAt: new Date(),
          ...(decision === 'REJECTED' && rejectionReason && { note: `${charge.note}\nRejected: ${rejectionReason}`.trim() }),
        },
      });

      if (decision === 'APPROVED') {
        await debitBookingAdjustmentTx(
          tx,
          charge.booking.userId,
          charge.bookingId,
          charge.amount,
          `${charge.type.toLowerCase()} pass-through charge`
        );
        await creditDriverAdjustmentTx(
          tx,
          charge.driverId,
          charge.bookingId,
          charge.amount,
          `${charge.type.toLowerCase()} reimbursement`
        );
      }

      await recordAudit(tx, {
        actor: { actorId: 'ADMIN', actorType: 'ADMIN' },
        action: 'BOOKING_EXTRA_CHARGE_REVIEWED',
        entityType: 'BookingExtraCharge',
        entityId: charge.id,
        oldValue: { status: charge.status },
        newValue: {
          status: decision,
          bookingId: charge.bookingId,
          amount: charge.amount,
          type: charge.type,
          rejectionReason,
        },
      });

      return reviewed;
    });

    res.json({ success: true, data: updated });
  } catch (err) {
    next(err);
  }
});

module.exports = router;
