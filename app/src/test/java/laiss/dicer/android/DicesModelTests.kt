package laiss.dicer.android

import laiss.dicer.android.model.Dice

class DicesModelTests {
    @Test
    fun singleDiceExpectations() {
        assertEquals(1.5, Dice.D2.expectation, 0.001)
        assertEquals(2.5, Dice.D4.expectation, 0.001)
        assertEquals(3.5, Dice.D6.expectation, 0.001)
        assertEquals(4.5, Dice.D8.expectation, 0.001)
        assertEquals(5.5, Dice.D10.expectation, 0.001)
        assertEquals(6.5, Dice.D12.expectation, 0.001)
        assertEquals(10.5, Dice.D20.expectation, 0.001)
        assertEquals(50.5, Dice.D100.expectation, 0.001)
    }
    @Test
    fun singleDiceDispersions() {
        assertEquals(0.25, Dice.D2.dispersion, 0.001)
        assertEquals(2.25, Dice.D4.dispersion, 0.001)
        assertEquals(6.25, Dice.D6.dispersion, 0.001)
        assertEquals(12.25, Dice.D8.dispersion, 0.001)
        assertEquals(20.25, Dice.D10.dispersion, 0.001)
        assertEquals(30.25, Dice.D12.dispersion, 0.001)
        assertEquals(90.25, Dice.D20.dispersion, 0.001)
        assertEquals(2450.25, Dice.D100.dispersion, 0.001)
    }
}