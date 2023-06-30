package gr.aueb.cf3.tradingjournalapp

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.slf4j.LoggerFactory
import spock.lang.Specification

class TestSpec extends Specification {

    private ListAppender<ILoggingEvent> appender

    def setup() {
        startLogAppender()
        appender.list.clear()
    }

    protected boolean assertLog(Level level, String message) {
        return appender.list.any {
            it.getFormattedMessage().contains(message) && it.getLevel() == level
        }
    }

    protected boolean assertLogCount(Level level, String message, Integer count) {
        return appender.list.findAll {
            it.getFormattedMessage().contains(message) && it.getLevel() == level
        }.size() == count
    }

    private startLogAppender() {
        Logger logger = (Logger) LoggerFactory.getLogger(TradingJournalAppApplication.class.package.name)
        appender = new ListAppender<>()
        appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory())
        logger.addAppender(appender)
        appender.start()
    }

}


