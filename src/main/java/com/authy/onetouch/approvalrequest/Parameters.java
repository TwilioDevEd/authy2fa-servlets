package com.authy.onetouch.approvalrequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class Parameters {

    private List<NameValuePair> parameters;

    protected Parameters() {
        this.parameters = new ArrayList<>();
    }

    public static Builder builder() {
        return new Parameters.Builder();
    }

    /**
     * Obtain the parameters for the client.
     *
     * @return The parameters
     */
    public List<NameValuePair> getParams() {
        return this.parameters;
    }

    public static class Builder {
        private Parameters instance = new Parameters();

        public Builder() {
        }

        /**
         * Add a detail that will be shown to the user.
         *
         * @param name    The name or key
         * @param value   The value
         *
         * @return An instance of {@link Builder}
         */
        public Builder addDetail(String name, String value) {
            instance.getParams()
                    .add(new BasicNameValuePair(String.format("details[%s]", name), value));
            return this;
        }

        /**
         * Add an approval request detail that will be hidden to user.
         *
         * @param name    The name or key
         * @param value   The value
         *
         * @return An instance of {@link Builder}
         */
        public Builder addHiddenDetail(String name, String value) {
            instance.getParams()
                    .add(new BasicNameValuePair(String.format("hidden_details[%s]", name), value));
            return this;
        }

        /**
         * Set the number of seconds that the approval request will be available
         * for being responded.
         *
         * <p>
         *     If this value is not set, defaults to 86400 (one day)
         *     If set to 0, the approval request won't expire
         * </p>
         *
         * @param secondsToExpire   The seconds to expire
         *
         * @return An instance of {@link Builder}
         */
        public Builder setSecondsToExpire(int secondsToExpire) {
            instance.getParams()
                    .add(new BasicNameValuePair("seconds_to_expire", Integer.toString(secondsToExpire)));
            return this;
        }

        /**
         * Builds a {@link Parameters} instance.
         *
         * @return An instance of {@link Parameters}
         */
        public Parameters build() {
            return instance;
        }
    }

}
