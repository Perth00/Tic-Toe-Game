package ch.util

import scalafx.scene.media.AudioClip

class AudioManager {
  val moveSound: AudioClip = new AudioClip(getClass.getResource("/Media/BotclickSound.aiff").toExternalForm)
  val warningSound: AudioClip = new AudioClip(getClass.getResource("/Media/WarningEffect.mp3").toExternalForm)
  val missClick: AudioClip = new AudioClip(getClass.getResource("/Media/MissClick.mp3").toExternalForm)
  val success: AudioClip = new AudioClip(getClass.getResource("/Media/success.mp3").toExternalForm)
  val lose: AudioClip = new AudioClip(getClass.getResource("/Media/lose.mp3").toExternalForm)
  val playerClick: AudioClip = new AudioClip(getClass.getResource("/Media/clickbutton.mp3").toExternalForm)
  val Click: AudioClip = new AudioClip(getClass.getResource("/Media/Clickeffect.aiff").toExternalForm)

}
