import {ApolloClient, HttpLink, InMemoryCache, split} from '@apollo/client'
import {getMainDefinition} from "@apollo/client/utilities";
import WebSocketLink from "./WebSocketLink";
import {isClient, isServer} from "../utils/env";

const httpLink = new HttpLink({
    uri: 'http://localhost:8080/graphql'
});

const splitLink = isClient ? split(
    ({query}) => {
        const definition = getMainDefinition(query);
        return (
            definition.kind === 'OperationDefinition' &&
            definition.operation === 'subscription'
        );
    },
    new WebSocketLink({
        url: 'ws://localhost:8080/subscriptions',
        retryAttempts: 10,
    }),
    httpLink,
) : httpLink

const client = new ApolloClient({
    link: splitLink,
    cache: new InMemoryCache(),
    ssrMode: isServer
});

export default client